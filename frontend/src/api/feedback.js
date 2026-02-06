import http from './http'

export const addFeedback = (content, type) =>
  http.post('/feedback/add', null, { params: { content, type } })
export const getMyFeedback = () => http.get('/feedback/my')
