import http from './http'

export const getAdminNotices = () => http.get('/notice/list')
export const addAdminNotice = (data) => http.post('/admin/notice/add', data)
export const updateAdminNotice = (data) => http.post('/admin/notice/update', data)
export const deleteAdminNotice = (id) => http.post('/admin/notice/delete', null, { params: { id } })

export const getAdminFeedback = (params) => http.get('/admin/feedback/list', { params })
export const updateAdminFeedback = (data) => http.post('/admin/feedback/update', data)
export const updateAdminFeedbackStatus = (id, status) =>
  http.post('/admin/feedback/updateStatus', null, { params: { id, status } })

export const getAdminProducts = () => http.get('/admin/product/list')
export const updateAdminProductStatus = (productId, status) =>
  http.post('/admin/product/updateStatus', null, { params: { productId, status } })

export const getAdminOrders = () => http.get('/admin/order/list')
export const updateAdminOrderStatus = (orderId, orderStatus) =>
  http.post('/admin/order/updateStatus', null, { params: { orderId, orderStatus } })

export const getAdminUsers = () => http.get('/admin/user/list')
export const updateAdminUserStatus = (userId, status) =>
  http.post('/admin/user/updateStatus', null, { params: { userId, status } })

export const getAdminCarousels = () => http.get('/admin/carousel/list')
export const addAdminCarousel = (data) => http.post('/admin/carousel/add', data)
export const updateAdminCarousel = (data) => http.post('/admin/carousel/update', data)
export const deleteAdminCarousel = (id) => http.post('/admin/carousel/delete', null, { params: { id } })

export const getAdminCategories = () => http.get('/admin/category/list')
export const addAdminCategory = (data) => http.post('/admin/category/add', data)
export const updateAdminCategory = (data) => http.post('/admin/category/update', data)
export const deleteAdminCategory = (id) => http.post('/admin/category/delete', null, { params: { id } })

export const getAdminCircleCategories = () => http.get('/admin/circleCategory/list')
export const addAdminCircleCategory = (data) => http.post('/admin/circleCategory/add', data)
export const updateAdminCircleCategory = (data) => http.post('/admin/circleCategory/update', data)
export const deleteAdminCircleCategory = (id) =>
  http.post('/admin/circleCategory/delete', null, { params: { id } })

export const getAdminDashboard = () => http.get('/admin/statistics/dashboard')
export const getAdminTrend = (days = 7) => http.get('/admin/statistics/trend', { params: { days } })
