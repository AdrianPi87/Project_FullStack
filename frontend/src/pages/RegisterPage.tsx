import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useRegisterMutation, useMergeCartsMutation } from '../api/productApi'
import { getSessionId } from '../utils/session'

export default function RegisterPage() {
  const navigate = useNavigate()
  const [register, { isLoading, error }] = useRegisterMutation()
  const [mergeCarts] = useMergeCartsMutation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [message, setMessage] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setMessage(null)
    const anonymousSessionId = getSessionId()
    try {
      const res = await register({ email, password, displayName }).unwrap()
      localStorage.setItem('configurator.token', res.token)
      if (anonymousSessionId && anonymousSessionId !== String(res.userId)) {
        try {
          await mergeCarts({ sourceSessionId: anonymousSessionId, targetSessionId: String(res.userId) }).unwrap()
        } catch {
          // keep registration flow resilient
        }
      }
      localStorage.setItem('configurator.sessionId', String(res.userId))
      localStorage.setItem('configurator.role', res.role)
      setMessage(`Account created: ${res.email}`)
      setTimeout(() => navigate('/'), 500)
    } catch (err: any) {
      setMessage(err?.data?.message || 'Registration failed')
    }
  }

  return (
    <div className="mx-auto max-w-md">
      <div className="rounded-md border border-zinc-200 bg-white p-8">
        <h1 className="text-2xl font-semibold tracking-tight">Register</h1>
        <p className="mt-1 text-sm text-zinc-600">Create an account to save carts and orders.</p>
        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <label className="block">
            <span className="text-sm font-medium">Display name</span>
            <input
              value={displayName}
              onChange={(e) => setDisplayName(e.target.value)}
              className="mt-1 w-full rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
            />
          </label>
          <label className="block">
            <span className="text-sm font-medium">Email</span>
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              type="email"
              required
              className="mt-1 w-full rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
            />
          </label>
          <label className="block">
            <span className="text-sm font-medium">Password</span>
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              required
              className="mt-1 w-full rounded-md border border-zinc-200 px-3 py-2 text-sm focus:border-black focus:outline-none"
            />
          </label>
          <div className="flex items-center gap-3">
            <button type="submit" disabled={isLoading} className="rounded-md bg-black px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-800 disabled:opacity-60">
              {isLoading ? 'Creating…' : 'Register'}
            </button>
          </div>
          {message && <p className={`mt-2 text-sm ${error ? 'text-red-700' : 'text-green-700'}`}>{message}</p>}
        </form>
      </div>
    </div>
  )
}
