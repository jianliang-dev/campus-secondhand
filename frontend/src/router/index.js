import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import MainLayout from '../layouts/MainLayout.vue'
import Home from '../views/Home.vue'
import BrowseProducts from '../views/BrowseProducts.vue'
import ProductDetail from '../views/ProductDetail.vue'
import PublishProduct from '../views/PublishProduct.vue'
import MyProducts from '../views/MyProducts.vue'
import MyFavorites from '../views/MyFavorites.vue'
import Community from '../views/Community.vue'
import PostDetail from '../views/PostDetail.vue'
import Wanted from '../views/Wanted.vue'
import MyWanted from '../views/MyWanted.vue'
import Orders from '../views/Orders.vue'
import Address from '../views/Address.vue'
import Feedback from '../views/Feedback.vue'
import Chat from '../views/Chat.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminHome from '../views/admin/AdminHome.vue'
import AdminNotice from '../views/admin/AdminNotice.vue'
import AdminFeedback from '../views/admin/AdminFeedback.vue'
import AdminProducts from '../views/admin/AdminProducts.vue'
import AdminOrders from '../views/admin/AdminOrders.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import AdminCarousels from '../views/admin/AdminCarousels.vue'
import AdminCategories from '../views/admin/AdminCategories.vue'
import AdminCircleCategories from '../views/admin/AdminCircleCategories.vue'
import AdminStatistics from '../views/admin/AdminStatistics.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', name: 'login', component: Login },
    { path: '/register', name: 'register', component: Register },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true, requiresUser: true },
      children: [
        { path: 'home', name: 'home', component: Home },
        { path: 'products', name: 'products', component: BrowseProducts },
        { path: 'product/:id', name: 'product-detail', component: ProductDetail },
        { path: 'publish', name: 'publish', component: PublishProduct },
        { path: 'my/products', name: 'my-products', component: MyProducts },
        { path: 'my/favorites', name: 'my-favorites', component: MyFavorites },
        { path: 'community', name: 'community', component: Community },
        { path: 'post/:id', name: 'post-detail', component: PostDetail },
        { path: 'wanted', name: 'wanted', component: Wanted },
        { path: 'my/wanted', name: 'my-wanted', component: MyWanted },
        { path: 'orders', name: 'orders', component: Orders },
        { path: 'address', name: 'address', component: Address },
        { path: 'feedback', name: 'feedback', component: Feedback },
        { path: 'chat', name: 'chat', component: Chat },
      ],
    },
    {
      path: '/admin',
      component: AdminLayout,
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', name: 'admin-home', component: AdminHome },
        { path: 'notices', name: 'admin-notices', component: AdminNotice },
        { path: 'feedback', name: 'admin-feedback', component: AdminFeedback },
        { path: 'products', name: 'admin-products', component: AdminProducts },
        { path: 'orders', name: 'admin-orders', component: AdminOrders },
        { path: 'users', name: 'admin-users', component: AdminUsers },
        { path: 'categories', name: 'admin-categories', component: AdminCategories },
        { path: 'circle-categories', name: 'admin-circle-categories', component: AdminCircleCategories },
        { path: 'carousels', name: 'admin-carousels', component: AdminCarousels },
        { path: 'statistics', name: 'admin-statistics', component: AdminStatistics },
      ],
    },
  ],
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some((record) => record.meta?.requiresAuth)
  const requiresAdmin = to.matched.some((record) => record.meta?.requiresAdmin)
  const requiresUser = to.matched.some((record) => record.meta?.requiresUser)
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.name === 'login' || to.name === 'register') {
    return next()
  }

  if (requiresAuth) {
    if (!token) {
      return next('/login')
    }
  }
  if (requiresAdmin) {
    if (!user || user.role !== 1) {
      return next('/home')
    }
  }
  if (requiresUser) {
    if (user && user.role === 1) {
      return next('/admin')
    }
  }
  next()
})

export default router
