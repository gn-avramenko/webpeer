import { UserConfig, ConfigEnv } from 'vite';
import { resolve } from 'path';
import dts from 'vite-plugin-dts';

// https://vitejs.dev/config/
const defineConfig = ({ mode, command }: ConfigEnv): UserConfig => {
  const config = {
    plugins: [
      dts(),
    ],
    build: {
      lib: {
        entry: resolve('src', 'index.tsx'),
        name: 'webpeer-antd',
        formats: ['es'], // Форматы ES Modules
        fileName: (format) => 'index.js',
      },
    },
    resolve: {
      alias: {
        '@': resolve(__dirname, './src'), // Базовый алиас для папки src
      },
    },
    sourcemap: true,
    // Reduce bloat from legacy polyfills.
    target: 'esnext',
    // Leave minification up to applications.
    minify: false,
  };
  // @ts-ignore
  return config;
};
export default defineConfig;
