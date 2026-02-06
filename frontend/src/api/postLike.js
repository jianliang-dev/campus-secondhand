import http from './http'

export const checkPostLike = (postId) =>
  http.get('/postLike/check', { params: { postId } })

export const togglePostLike = (postId) =>
  http.post('/postLike/toggle', null, { params: { postId } })
