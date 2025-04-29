module.exports = {
  root: true,
  env: {
    browser: true,
    es2021: true,
  },
  extends: [
    'airbnb',
  ],
  parser: '@typescript-eslint/parser',
  parserOptions: {
    ecmaVersion: 2022,
    sourceType: 'module',
  },
  plugins: [
    '@typescript-eslint',
  ],
  rules: {
    'react/jsx-filename-extension': [2, { extensions: ['.js', '.ts', '.tsx', '.jsx'] }],
    'import/extensions': 'off',
    'no-unused-vars': 'off',
    'no-nested-ternary': 'off',
    'import/no-extraneous-dependencies': 'off',
    'max-classes-per-file': 'off',
    'no-use-before-define': 'off',
    'prefer-destructuring': 'off',
    'import/prefer-default-export': 'off',
    'max-len': 'off',
    'import/no-relative-packages': 'off',
    'no-param-reassign': 'off',
    'react/react-in-jsx-scope': 'off',
    'react/jsx-no-useless-fragment': 'off',
    'react/jsx-closing-tag-location': 'off',
    'react/destructuring-assignment': 'off',
    'import/no-unresolved': 'off',
    'class-methods-use-this': 'off',
    'react/function-component-definition': [
      2,
      {
        namedComponents: 'function-declaration',
      },
    ],
  },
};
