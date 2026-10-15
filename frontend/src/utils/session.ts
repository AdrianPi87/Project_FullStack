const STORAGE_KEY = 'configurator.sessionId'

export function getSessionId(): string {
  let id = localStorage.getItem(STORAGE_KEY)
  if (!id) {
    id = `sess_${Math.random().toString(36).slice(2)}_${Date.now()}`
    localStorage.setItem(STORAGE_KEY, id)
  }
  return id
}
