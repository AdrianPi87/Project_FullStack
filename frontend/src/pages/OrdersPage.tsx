import { useGetOrdersQuery } from '../api/productApi'
import { getSessionId } from '../utils/session'

export default function OrdersPage() {
  const sessionId = getSessionId()
  const { data: orders, isLoading, error } = useGetOrdersQuery(sessionId)
  const hasToken = Boolean(localStorage.getItem('configurator.token'))

  if (isLoading) return <p className="text-sm text-zinc-500">Loading orders…</p>
  if (error) return <p className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">Failed to load orders.</p>

  return (
    <div>
      <h1 className="text-2xl font-semibold tracking-tight">Your Orders</h1>

      {!hasToken && (
        <div className="mt-6 rounded-md border border-zinc-200 bg-white p-6 text-sm text-zinc-600">
          Please <a href="/login" className="font-medium text-black underline">login</a> to view orders.
        </div>
      )}

      {hasToken && (!orders || orders.length === 0) ? (
        <div className="mt-6 rounded-md border border-zinc-200 bg-white p-6 text-sm text-zinc-600">
          No orders yet. Complete a checkout from your cart to see them here.
        </div>
      ) : hasToken ? (
        <div className="mt-6 space-y-4">
          {orders?.map((order) => (
            <article key={order.id} className="rounded-md border border-zinc-200 bg-white p-6">
              <div className="flex items-center justify-between">
                <p className="text-sm font-medium">{order.id}</p>
                <span className="rounded-full border border-zinc-200 bg-zinc-50 px-2.5 py-0.5 text-xs font-medium">{order.status}</span>
              </div>
              <p className="mt-1 text-xs text-zinc-500">{new Date(order.createdAt).toLocaleString()}</p>
              <div className="mt-4 divide-y divide-zinc-100 text-sm">
                {order.items.map((item) => (
                  <div key={item.id} className="flex items-center justify-between py-2">
                    <span className="text-zinc-600">{item.productName} × {item.quantity}</span>
                    <span className="font-medium">${item.lineTotal.toFixed(2)}</span>
                  </div>
                ))}
              </div>
              <p className="mt-4 text-right text-base font-semibold">Total: ${order.total.toFixed(2)}</p>
            </article>
          ))}
        </div>
      ) : null}
    </div>
  )
}
