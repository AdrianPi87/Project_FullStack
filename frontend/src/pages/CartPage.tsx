import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  useCheckoutMutation,
  useGetCartQuery,
  useRecordEventMutation,
  useRemoveCartItemMutation,
  useUpdateCartItemMutation,
} from '../api/productApi'
import { getSessionId } from '../utils/session'

export default function CartPage() {
  const sessionId = getSessionId()
  const navigate = useNavigate()
  const { data: cart, isLoading } = useGetCartQuery(sessionId)
  const [updateCartItem] = useUpdateCartItemMutation()
  const [removeCartItem] = useRemoveCartItemMutation()
  const [checkout, checkoutState] = useCheckoutMutation()
  const [recordEvent] = useRecordEventMutation()
  const [message, setMessage] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const hasToken = Boolean(localStorage.getItem('configurator.token'))

  const handleCheckout = async () => {
    setError(null)
    setMessage(null)
    try {
      const order = await checkout(sessionId).unwrap()
      recordEvent({ type: 'checkout_completed', entityId: order.id, payload: JSON.stringify(order.total), sessionId })
      setMessage(`Order created: ${order.id}`)
      setTimeout(() => navigate('/orders'), 800)
    } catch (e: any) {
      setError(e?.data?.message || 'Checkout failed. Cart may be empty.')
    }
  }

  if (isLoading) return <p className="text-sm text-zinc-500">Loading cart…</p>

  return (
    <div>
      <h1 className="text-2xl font-semibold tracking-tight">Cart</h1>

      {!hasToken && (
        <div className="mt-6 rounded-md border border-zinc-200 bg-white p-6 text-sm text-zinc-600">
          Please <a href="/login" className="font-medium text-black underline">login</a> to add and manage cart items.
        </div>
      )}

      {hasToken && (!cart || cart.items.length === 0) ? (
        <div className="mt-6 rounded-md border border-zinc-200 bg-white p-6 text-sm text-zinc-600">
          Your cart is empty. Add products from the catalog to see them here.
        </div>
      ) : hasToken ? (
        <>
          <div className="mt-6 space-y-4">
            {cart?.items.map((item) => (
              <div key={item.id} className="flex flex-col gap-4 rounded-md border border-zinc-200 bg-white p-5 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <p className="font-medium">{item.productName}</p>
                  <p className="mt-1 text-xs text-zinc-500">Options: {item.selectedOptionIds.join(', ') || 'none'}</p>
                  <p className="mt-1 text-sm text-zinc-600">${item.unitPrice.toFixed(2)} each</p>
                </div>
                <div className="flex items-center gap-3">
                  <input
                    type="number"
                    min={1}
                    value={item.quantity}
                    className="w-16 rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
                    onChange={(e) => updateCartItem({ sessionId, itemId: item.id, quantity: Math.max(1, Number(e.target.value) || 1) })}
                  />
                  <span className="min-w-[72px] text-right font-medium">${item.lineTotal.toFixed(2)}</span>
                  <button
                    onClick={() => removeCartItem({ sessionId, itemId: item.id })}
                    className="rounded-md border border-zinc-200 bg-zinc-100 px-3 py-2 text-sm hover:bg-zinc-200"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))}
          </div>

          <div className="mt-6 flex flex-col items-end gap-3">
            <p className="text-lg font-semibold">Total: ${cart?.total.toFixed(2)}</p>
            <div className="flex items-center gap-3">
              <button
                onClick={handleCheckout}
                disabled={checkoutState.isLoading}
                className="rounded-md bg-black px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60"
              >
                {checkoutState.isLoading ? 'Processing…' : 'Checkout'}
              </button>
            </div>
          </div>
        </>
      ) : null}
      {message && <p className="mt-4 rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">{message}</p>}
      {error && <p className="mt-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</p>}
    </div>
  )
}
