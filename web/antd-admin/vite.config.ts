import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';
import dts from 'vite-plugin-dts';
// @ts-ignore
import { peerDependencies, dependencies } from './package.json';

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
  const config = {
    plugins: [
      dts(),
    ],
    build: {
      lib: {
        entry: resolve('src', 'index.ts'),
        name: 'webpeer-antd-admin',
        formats: ['es'], // Форматы ES Modules
        fileName: (format) => 'index.js',
      },
      // Leave minification up to applications.
      minify: false,
      sourcemap: true,
      rollupOptions: {
        // Автоматически исключаем все зависимости из package.json
        external: [
          ...Object.keys(dependencies || {}),
          ...Object.keys(peerDependencies || {}),
        ],
      },
    },
    resolve: {
      alias: {
        'webpeer-core': resolve(__dirname, '../core/src/index'), // Базовый алиас для папки src
        'webpeer-antd': resolve(__dirname, '../antd/src/index'), // Базовый алиас для папки src
      },
    },
    sourcemap: true,
    // Reduce bloat from legacy polyfills.
    target: 'esnext',

  };
  // @ts-ignore
  return config;
};
export default defineConfig;
