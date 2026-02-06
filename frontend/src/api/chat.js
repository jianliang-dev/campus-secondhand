import http from './http'

export const getChatSessions = (keyword) =>
  http.get('/chat/sessions', { params: keyword ? { keyword } : {} })
export const getChatHistory = (sessionId) =>
  http.get('/chat/history', { params: { sessionId } })
export const readSession = (sessionId) =>
  http.post('/chat/read', null, { params: { sessionId } })
export const getOrCreateSession = (otherUserId) =>
  http.post('/chat/session', null, { params: { otherUserId } })
