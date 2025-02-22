import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';
import dts from 'vite-plugin-dts';
import {fileURLToPath, URL} from "url";

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {

    const config = {
        server: {
            open: true,
            proxy: {
                '/_ui': {
                    target: 'http://localhost:8080/',
                    changeOrigin: false,
                },
            },
        },
        plugins: [
            dts({
                exclude: ['src/main.tsx'],
            }),
        ],
        build: {
            commonjsOptions: {
                include: ["@webpeer/core", /node_modules/],
            },
            rollupOptions: {
                input: {
                    app: './index.html', // default
                },
            },
        },
        resolve: {
            alias: [
                {
                    find: '@',
                    replacement: fileURLToPath(new URL('./src', import.meta.url)),
                },
                {
                    find: '@webpeer/core',
                    replacement: fileURLToPath(new URL("../core/src/index.ts", import.meta.url)),
                },
            ],
        },
        optimizeDeps: {
            include: ['@webpeer/core'],
        },
    };
    return config;
};
export default defineConfig;