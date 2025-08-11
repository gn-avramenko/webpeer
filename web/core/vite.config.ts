import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';
import dts from 'vite-plugin-dts';

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
    return {
        plugins: [dts()],
        build: {
            lib: {
                entry: resolve('src', 'index.ts'),
                name: 'webpeer-core',
                formats: ['es'], // Форматы ES Modules
                fileName: (format) => `index.js`,
            },
            sourcemap: true,
            // Reduce bloat from legacy polyfills.
            target: 'esnext',
            // Leave minification up to applications.
            minify: false,
        },
    };
};

export default defineConfig;
