import http from './http'

export const getPosts = (params) => http.get('/post/list', { params })
export const getHotPosts = (params) => http.get('/post/hot', { params })
export const getPostDetail = (id) => http.get(`/post/${id}`)
export const publishPost = (data) => http.post('/post/publish', data)
export const getMyPosts = () => http.get('/post/my')
export const updatePost = (data) => http.post('/post/update', data)
export const deletePost = (id) => http.post('/post/delete', null, { params: { id } })
