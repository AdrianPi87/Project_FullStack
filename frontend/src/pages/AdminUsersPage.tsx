import { Link } from 'react-router-dom'
import { useGetAdminUsersQuery } from '../api/productApi'

export default function AdminUsersPage() {
  const isAdmin = localStorage.getItem('configurator.role') === 'ADMIN'
  const { data: users, isLoading, error } = useGetAdminUsersQuery(undefined, { skip: !isAdmin })

  if (!isAdmin) {
    return (
      <div className="mx-auto max-w-3xl px-6 py-10">
        <div className="rounded-md border border-zinc-200 bg-white p-6">
          <h1 className="text-xl font-semibold">Admin area</h1>
          <p className="mt-2 text-sm text-zinc-600">You need an administrator account to view this page.</p>
          <Link to="/login" className="mt-4 inline-block rounded-md bg-black px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800">
            Login
          </Link>
        </div>
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-7xl px-6 py-10">
      <h1 className="text-2xl font-semibold tracking-tight">Users</h1>
      <p className="mt-1 text-sm text-zinc-600">Registered accounts</p>

      <div className="mt-6 overflow-hidden rounded-md border border-zinc-200 bg-white">
        <table className="min-w-full divide-y divide-zinc-200 text-sm">
          <thead className="bg-zinc-50">
            <tr>
              <th className="px-4 py-3 text-left font-medium text-zinc-600">ID</th>
              <th className="px-4 py-3 text-left font-medium text-zinc-600">Email</th>
              <th className="px-4 py-3 text-left font-medium text-zinc-600">Name</th>
              <th className="px-4 py-3 text-left font-medium text-zinc-600">Role</th>
              <th className="px-4 py-3 text-left font-medium text-zinc-600">Created</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-zinc-100">
            {isLoading && (
              <tr><td colSpan={5} className="px-4 py-6 text-zinc-500">Loading…</td></tr>
            )}
            {error && (
              <tr><td colSpan={5} className="px-4 py-6 text-red-600">Failed to load users.</td></tr>
            )}
            {users?.map((u) => (
              <tr key={u.id}>
                <td className="px-4 py-3">{u.id}</td>
                <td className="px-4 py-3">{u.email}</td>
                <td className="px-4 py-3">{u.displayName || '—'}</td>
                <td className="px-4 py-3">
                  <span className="inline-flex rounded-full border border-zinc-200 bg-zinc-50 px-2.5 py-0.5 text-xs font-medium">
                    {u.role}
                  </span>
                </td>
                <td className="px-4 py-3 text-zinc-500">{new Date(u.createdAt).toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
