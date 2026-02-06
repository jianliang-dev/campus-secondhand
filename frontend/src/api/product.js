import http from './http'

export const getProducts = () => http.get('/product/list')
export const getProductDetail = (id) => http.get(`/product/${id}`)
export const searchProducts = (keyword) => http.get('/product/search', { params: { keyword } })
export const getRecommend = (limit) => http.get('/product/recommend', { params: { limit } })
export const publishProduct = (data) => http.post('/product/publish', data)
export const getMyProducts = () => http.get('/product/my')
export const updateProduct = (data) => http.post('/product/update', data)
export const deleteProduct = (productId) => http.post('/product/delete', null, { params: { productId } })
export const updateProductStatus = (productId, status) =>
  http.post('/product/updateStatus', null, { params: { productId, status } })
export const uploadProductCover = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.post('/product/uploadCover', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
}
