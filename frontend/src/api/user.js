import http from './http'

export const login = (username, password) =>
  http.post('/user/login', null, { params: { username, password } })

export const register = (username, password, phone) =>
  http.post('/user/register', null, { params: { username, password, phone } })

export const profile = () => http.get('/user/profile')

export const updateProfile = (data) => http.post('/user/updateProfile', data)
