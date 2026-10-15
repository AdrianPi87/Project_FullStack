import { Link, Navigate, Route, Routes, useNavigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import HomePage from './pages/HomePage'
import ProductsPage from './pages/ProductsPage'
import ConfiguratorPage from './pages/ConfiguratorPage'
import CartPage from './pages/CartPage'
import OrdersPage from './pages/OrdersPage'
import AdminUsersPage from './pages/AdminUsersPage'

export default function App() {
  const navigate = useNavigate()
  const token = localStorage.getItem('configurator.token')
  const role = localStorage.getItem('configurator.role')

  const logout = () => {
    localStorage.removeItem('configurator.token')
    localStorage.removeItem('configurator.sessionId')
    localStorage.removeItem('configurator.role')
    navigate('/')
    window.location.reload()
  }

  return (
    <div className="min-h-screen bg-[#fafafa] text-[#09090b]">
      <header className="border-b border-zinc-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <Link to="/" className="text-base font-semibold tracking-tight">Aurora Products</Link>
          <nav className="flex items-center gap-6 text-sm">
            <Link to="/" className="text-zinc-600 hover:text-black">Home</Link>
            <Link to="/products" className="text-zinc-600 hover:text-black">Products</Link>
            <Link to="/cart" className="text-zinc-600 hover:text-black">Cart</Link>
            <Link to="/orders" className="text-zinc-600 hover:text-black">Orders</Link>
            {role === 'ADMIN' && <Link to="/admin/users" className="text-zinc-600 hover:text-black">Admin</Link>}
            {!token && <Link to="/login" className="text-zinc-600 hover:text-black">Login</Link>}
            {!token && <Link to="/register" className="text-zinc-600 hover:text-black">Register</Link>}
            {token && (
              <button
                onClick={logout}
                className="rounded-md border border-zinc-200 bg-zinc-100 px-3 py-1.5 text-sm hover:bg-zinc-200"
              >
                Logout
              </button>
            )}
          </nav>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-6 py-8">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/products/:id" element={<ConfiguratorPage />} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/admin/users" element={role === 'ADMIN' ? <AdminUsersPage /> : <Navigate to="/login" replace />} />
        </Routes>
      </main>
    </div>
  )
}
