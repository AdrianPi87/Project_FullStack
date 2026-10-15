import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { useGetProductsQuery } from '../api/productApi'

export default function ProductsPage() {
  const { data: products, isLoading, error } = useGetProductsQuery()
  const [query, setQuery] = useState('')
  const [category, setCategory] = useState<string>('all')
  const [sort, setSort] = useState<'name' | 'price-asc' | 'price-desc'>('name')

  const categories = useMemo(() => {
    const set = new Set(products?.map((p) => p.category || 'Uncategorized') ?? [])
    return ['all', ...Array.from(set).sort()]
  }, [products])

  const filtered = useMemo(() => {
    const list = products?.filter((p) => {
      const matchesQuery =
        !query ||
        p.name.toLowerCase().includes(query.toLowerCase()) ||
        p.description?.toLowerCase().includes(query.toLowerCase())
      const matchesCategory = category === 'all' || (p.category || 'Uncategorized') === category
      return matchesQuery && matchesCategory
    }) ?? []

    list.sort((a, b) => {
      if (sort === 'price-asc') return a.basePrice - b.basePrice
      if (sort === 'price-desc') return b.basePrice - a.basePrice
      return a.name.localeCompare(b.name)
    })
    return list
  }, [products, query, category, sort])

  if (isLoading) {
    return <p className="text-sm text-zinc-500">Loading products…</p>
  }

  if (error) {
    return <p className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">Failed to load products. Is the backend running on :8080?</p>
  }

  return (
    <div>
      <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">Products</h1>
          <p className="mt-1 text-sm text-zinc-600">Choose a product and configure it to your needs.</p>
        </div>
        <div className="flex flex-col gap-3 sm:flex-row">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search products"
            className="w-full rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none sm:w-56"
          />
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
          >
            {categories.map((c) => (
              <option key={c} value={c}>
                {c === 'all' ? 'All categories' : c}
              </option>
            ))}
          </select>
          <select
            value={sort}
            onChange={(e) => setSort(e.target.value as 'name' | 'price-asc' | 'price-desc')}
            className="rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
          >
            <option value="name">Name</option>
            <option value="price-asc">Price: low to high</option>
            <option value="price-desc">Price: high to low</option>
          </select>
        </div>
      </div>

      {filtered.length === 0 ? (
        <p className="rounded-md border border-zinc-200 bg-white p-6 text-sm text-zinc-600">No products match your filters.</p>
      ) : (
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {filtered.map((p) => (
            <article key={p.id} className="overflow-hidden rounded-md border border-zinc-200 bg-white">
              {p.imageUrl && (
                <img src={p.imageUrl} alt={p.name} className="h-44 w-full object-cover" />
              )}
              <div className="p-5">
                <h2 className="text-base font-semibold">{p.name}</h2>
                <p className="mt-1 text-sm text-zinc-600">{p.description}</p>
                {p.category && <p className="mt-2 text-xs uppercase tracking-wide text-zinc-400">{p.category}</p>}
                <p className="mt-3 text-sm font-medium text-black">From ${p.basePrice.toFixed(2)}</p>
                <Link
                  to={`/products/${p.id}`}
                  className="mt-4 inline-flex items-center rounded-md bg-black px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800"
                >
                  Configure
                </Link>
              </div>
            </article>
          ))}
        </div>
      )}
    </div>
  )
}