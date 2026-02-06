import http from './http'

export const checkFavorite = (productId) =>
  http.get('/favorite/check', { params: { productId } })
export const toggleFavorite = (productId) =>
  http.post('/favorite/toggle', null, { params: { productId } })
export const getFavorites = () => http.get('/favorite/list')