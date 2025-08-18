import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
    // @ts-ignore
    const config = {
        server: {
            open: true,
            proxy: {
                '/_ui': {
                    target: 'http://localhost:8080/',
                    changeOrigin: false,
                },
                '/_resources': {
                    target: 'http://localhost:8080/',
                    changeOrigin: false,
                },
                '/websocket': {
                    // Proxy requests starting with /websocket
                    target: 'http://localhost:8080/', // Target backend server for WebSocket
                    ws: true, // Enable WebSocket proxying
                    changeOrigin: true, // Change the origin header to the target URL
                },
            },
        },
        build: {
            sourcemap: true,
            target: 'esnext',
            rollupOptions: {
                input: {
                    app: './index.html', // default
                },
            },
        },
        resolve: {
            alias: {
                'webpeer-core': resolve(__dirname, '../../web/core/src/index'), // Базовый алиас для папки src
            },
        },
        css: {
            preprocessorOptions: {
                scss: {
                    silenceDeprecations: [
                        'import',
                        'mixed-decls',
                        'color-functions',
                        'global-builtin',
                    ],
                },
            },
        },
    };
    return config;
};
export default defineConfig;
