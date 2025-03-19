import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/Manager/home'},
    { path: '/Manager', component: import('../views/Manager.vue'),
      children: [
        { path: 'home', component: import('../views/Home.vue'),},
        { path: 'signal', component: () => import('../views/signal.vue'),},
        { path: 'about', component: import('../views/About.vue'),},
        { path: 'pgSQL', component: import('../views/pgSQL.vue'),},
        { path: 'leak-location1', component: import('../views/pd.vue'),},
        { path: 'leak-location2', component: import('../views/GCC.vue'),},
        { path: 'history', component: import('../views/history.vue'),},
        { path: 'leak-detection', component: import('../views/leak.vue'),},

      ]},

    { path: '/about', component: import('../views/About.vue'),},
    { path: '/notfound', component: import('../views/404.vue'),},
    { path: '/:pathMatch(.*)', redirect: '/notfound',},
  ],
})

export default router
