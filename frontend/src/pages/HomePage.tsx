import { Link } from 'react-router-dom'
import { useGetProductsQuery } from '../api/productApi'

const company = {
  name: 'Aurora Products',
  tagline: 'Premium products, configured for you.',
  description:
    'Aurora Products designs and assembles high-quality technology, sports and smart-home products. Configure each item to your exact needs, preview pricing in real time and order with confidence.',
}

export default function HomePage() {
  const { data: products } = useGetProductsQuery()

  return (
    <div className="space-y-12">
      <section className="overflow-hidden rounded-md border border-zinc-200 bg-white">
        <div className="grid gap-8 p-8 lg:grid-cols-2 lg:p-12">
          <div className="flex flex-col justify-center">
            <p className="text-sm font-medium uppercase tracking-wide text-zinc-500">{company.name}</p>
            <h1 className="mt-2 text-4xl font-semibold tracking-tight sm:text-5xl">{company.tagline}</h1>
            <p className="mt-4 text-sm text-zinc-600">{company.description}</p>
            <div className="mt-6 flex flex-wrap items-center gap-3">
              <Link to="/products" className="rounded-md bg-black px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800">
                Browse products
              </Link>
              <Link to="/register" className="rounded-md border border-zinc-200 bg-zinc-100 px-5 py-2.5 text-sm font-medium hover:bg-zinc-200">
                Create account
              </Link>
            </div>
          </div>
          <div className="grid grid-cols-2 gap-3">
            {products?.slice(0, 4).map((p) => (
              <Link key={p.id} to={`/products/${p.id}`} className="group overflow-hidden rounded-md border border-zinc-200">
                {p.imageUrl && <img src={p.imageUrl} alt={p.name} className="h-32 w-full object-cover transition group-hover:scale-105" />}
              </Link>
            ))}
          </div>
        </div>
      </section>

      <section>
        <div className="mb-6 flex items-center justify-between">
          <h2 className="text-xl font-semibold tracking-tight">Featured products</h2>
          <Link to="/products" className="text-sm font-medium text-black hover:underline">View all →</Link>
        </div>
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {products?.slice(0, 6).map((p) => (
            <article key={p.id} className="overflow-hidden rounded-md border border-zinc-200 bg-white">
              {p.imageUrl && <img src={p.imageUrl} alt={p.name} className="h-40 w-full object-cover" />}
              <div className="p-5">
                <h3 className="text-base font-semibold">{p.name}</h3>
                <p className="mt-1 text-sm text-zinc-600">{p.description}</p>
                <p className="mt-3 text-sm font-medium">From ${p.basePrice.toFixed(2)}</p>
                <Link to={`/products/${p.id}`} className="mt-4 inline-flex rounded-md bg-black px-4 py-2 text-sm font-medium text-white hover:bg-zinc-800">
                  Configure
                </Link>
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  )
}
