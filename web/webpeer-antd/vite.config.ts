import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';
import dts from 'vite-plugin-dts';
import {fileURLToPath, URL} from "url";

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
    const definedConfig = {};

    const config = {
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
                output: {
                    manualChunks: (
                        id: string
                    ) => {
                        if (id.indexOf("node_modules/@fedorov/chonky/") !== -1) {
                            return "chonky";
                        }
                    },
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
                    replacement: fileURLToPath(new URL("../webpeer-core/src/index.ts", import.meta.url)),
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