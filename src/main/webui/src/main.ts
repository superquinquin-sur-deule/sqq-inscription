import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import App from './App.vue'
import Registration from "./views/Registration.vue";


const routes = [
    { path: '/', component: Registration },
]

const router = createRouter({
    history: createWebHistory('/'),
    routes
})


const app = createApp(App)
app.use(router)
app.mount('#app')