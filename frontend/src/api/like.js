import http from './http'

export const checkLike = (productId) =>
  http.get('/like/check', { params: { productId } })
export const toggleLike = (productId) =>
  http.post('/like/toggle', null, { params: { productId } })
