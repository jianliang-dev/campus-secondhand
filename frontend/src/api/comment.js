import http from './http'

export const getComments = (targetId, targetType) =>
  http.get('/comment/list', { params: { targetId, targetType } })
export const addComment = (targetId, targetType, content) =>
  http.post('/comment/add', null, { params: { targetId, targetType, content } })
export const deleteComment = (id) => http.post('/comment/delete', null, { params: { id } })
