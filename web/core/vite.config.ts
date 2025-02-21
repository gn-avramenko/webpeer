import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';
import dts from 'vite-plugin-dts';

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
    return {
        plugins: [
            dts({
                exclude: ['src/main.tsx'],
            }),
        ],
        build: {
            lib: {
                entry: resolve('src', 'index.ts'),
                name: 'webpeer',
                fileName: (format) => `index.js`,
            },
            rollupOptions: {
                // external: ['react', 'react-dom'],
                // output: {
                //     globals: {
                //         react: 'React',
                //     },
                // },
            },
            sourcemap: true,
            // Reduce bloat from legacy polyfills.
            target: 'esnext',
            // Leave minification up to applications.
            minify: false,
        },
    };
}

export default defineConfig;