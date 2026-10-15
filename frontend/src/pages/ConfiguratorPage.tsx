import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useGetProductQuery, useConfigureMutation, useAddToCartMutation, useRecordEventMutation } from '../api/productApi'
import { getSessionId } from '../utils/session'
import type { OptionGroupResponse, OptionResponse } from '../types'

export default function ConfiguratorPage() {
  const { id } = useParams()
  const productId = Number(id)
  const { data: product, isLoading } = useGetProductQuery(productId)
  const [configure, configureState] = useConfigureMutation()
  const [addToCart, addToCartState] = useAddToCartMutation()
  const [recordEvent] = useRecordEventMutation()
  const [selected, setSelected] = useState<Set<number>>(new Set())
  const [quantity, setQuantity] = useState(1)
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  const sessionId = getSessionId()

  useEffect(() => {
    if (product) {
      const defaults = new Set<number>()
      product.optionGroups.forEach((group) => {
        group.options.forEach((opt) => {
          if (opt.defaultOption) defaults.add(opt.id)
        })
      })
      setSelected(defaults)
      recordEvent({ type: 'product_viewed', entityId: String(product.id), payload: product.name, sessionId })
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [product?.id])

  const toggleOption = (option: OptionResponse, group: OptionGroupResponse) => {
    setSelected((prev) => {
      const next = new Set(prev)
      if (group.selectionType === 'single') {
        group.options.forEach((o) => next.delete(o.id))
        next.add(option.id)
      } else {
        if (next.has(option.id)) {
          next.delete(option.id)
        } else {
          const currentCount = group.options.filter((o) => prev.has(o.id)).length
          if (currentCount < group.maxSelections) {
            next.add(option.id)
          }
        }
      }
      return next
    })
  }

  const handleAddToCart = async () => {
    setError(null)
    setMessage(null)
    try {
      const config = await configure({
        productId,
        selectedOptionIds: Array.from(selected),
        sessionId,
      }).unwrap()
      await addToCart({
        sessionId,
        productId,
        selectedOptionIds: Array.from(selected),
        quantity,
      }).unwrap()
      setMessage(`Added to cart. Total: $${config.totalPrice.toFixed(2)}`)
      recordEvent({ type: 'add_to_cart', entityId: String(productId), payload: JSON.stringify(Array.from(selected)), sessionId })
    } catch (e: any) {
      setError(e?.data?.message || e?.error || 'Unable to add to cart. Please check your selections and login.')
    }
  }

  useEffect(() => {
    if (!product) return
    const timer = window.setTimeout(() => {
      configure({ productId, selectedOptionIds: Array.from(selected), sessionId }).unwrap().catch(() => {})
    }, 350)
    return () => window.clearTimeout(timer)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selected, product?.id])

  if (isLoading || !product) {
    return <p className="text-sm text-zinc-500">Loading product…</p>
  }

  return (
    <div>
      <Link to="/" className="text-sm text-zinc-600 hover:text-black">← Back to products</Link>

      <div className="mt-6 grid gap-8 lg:grid-cols-2">
        {/* Product visor / simulator */}
        <section className="space-y-6">
          <div className="overflow-hidden rounded-md border border-zinc-200 bg-white">
            {product.imageUrl && (
              <img src={product.imageUrl} alt={product.name} className="h-72 w-full object-cover" />
            )}
            <div className="p-6">
              <h1 className="text-2xl font-semibold tracking-tight">{product.name}</h1>
              <p className="mt-2 text-sm text-zinc-600">{product.description}</p>
              <p className="mt-4 text-lg font-medium">Base price: ${product.basePrice.toFixed(2)}</p>
            </div>
          </div>

          {configureState.data && (
            <div className="rounded-md border border-zinc-200 bg-white p-6">
              <h2 className="text-base font-semibold">Price breakdown</h2>
              <p className="mt-2 text-2xl font-semibold">${configureState.data.totalPrice.toFixed(2)}</p>
              <div className="mt-4 divide-y divide-zinc-100 text-sm">
                {configureState.data.priceBreakdown?.map((line, idx) => (
                  <div key={idx} className="flex items-center justify-between py-2">
                    <span className="text-zinc-600">{line.label}</span>
                    <span className="font-medium">${line.amount.toFixed(2)}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </section>

        {/* Configuration panel */}
        <section className="space-y-6">
          {product.optionGroups.map((group) => (
            <div key={group.id} className="rounded-md border border-zinc-200 bg-white p-6">
              <div className="mb-4 flex items-center gap-2">
                <h3 className="text-base font-semibold">{group.displayName}</h3>
                {group.required && (
                  <span className="rounded-full border border-zinc-200 bg-zinc-50 px-2.5 py-0.5 text-xs font-medium text-zinc-600">
                    required
                  </span>
                )}
                <span className="rounded-full border border-zinc-200 bg-zinc-50 px-2.5 py-0.5 text-xs font-medium text-zinc-600">
                  {group.selectionType}
                </span>
              </div>
              <div className="space-y-3">
                {group.options.map((option) => {
                  const isSelected = selected.has(option.id)
                  const isDisabled = !option.active || option.stock <= 0
                  return (
                    <button
                      key={option.id}
                      type="button"
                      onClick={() => toggleOption(option, group)}
                      disabled={isDisabled}
                      className={`w-full rounded-md border-2 p-4 text-left transition ${
                        isDisabled
                          ? 'cursor-not-allowed border-zinc-100 bg-zinc-50 opacity-50'
                          : isSelected
                            ? 'border-black bg-zinc-50'
                            : 'border-zinc-200 bg-white hover:border-zinc-400'
                      }`}
                    >
                      <div className="flex items-center justify-between gap-4">
                        <div>
                          <p className="text-sm font-medium">{option.name}</p>
                          <p className="mt-0.5 text-xs text-zinc-500">{option.description}</p>
                          {option.stock <= 5 && option.stock > 0 && (
                            <p className="mt-1 text-xs text-zinc-500">Low stock: {option.stock}</p>
                          )}
                        </div>
                        <span className="text-sm font-medium">
                          {option.priceDelta >= 0 ? '+' : ''}
                          ${option.priceDelta.toFixed(2)}
                        </span>
                      </div>
                    </button>
                  )
                })}
              </div>
            </div>
          ))}

          <div className="rounded-md border border-zinc-200 bg-white p-6">
            <div className="flex items-center gap-3">
              <label className="text-sm font-medium">Quantity</label>
              <input
                type="number"
                min={1}
                value={quantity}
                onChange={(e) => setQuantity(Math.max(1, Number(e.target.value) || 1))}
                className="w-20 rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
              />
            </div>
            <div className="mt-4 flex flex-wrap items-center gap-3">
              <button
                onClick={handleAddToCart}
                disabled={addToCartState.isLoading || configureState.isLoading}
                className="rounded-md bg-black px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
              >
                {addToCartState.isLoading ? 'Adding…' : 'Validate & Add to Cart'}
              </button>
            </div>
            {message && <p className="mt-4 rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">{message}</p>}
            {error && <p className="mt-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}
          </div>
        </section>
      </div>
    </div>
  )
}
