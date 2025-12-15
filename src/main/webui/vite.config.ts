import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import VitePluginUmami from "./src/utils/umami";

// https://vite.dev/config/
export default defineConfig({
    base: '',
    plugins: [
        vue(),
        VitePluginUmami({
            enableDev: false,
            umami: {
                id: "28ad4313-919a-4fe1-a6ba-ef7b7442e7b4",
                src: "https://umami.hoohoot.org/script.js"
            }
        })
    ],
    server: {
        port: 3000,
    },
})
