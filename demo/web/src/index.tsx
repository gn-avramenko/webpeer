import 'webpeer-core';
import { demoWebPeerExt } from './common.ts';
import { DemoRootElementFactory } from './demo-root.tsx';
import './styles.scss';
import { DemoTextFieldElementFactory } from './demo-text-field.tsx';
import { DemoMessagesElementFactory } from './demo-messages-area.tsx';
import { DemoButtonElementFactory } from './demo-button.tsx';

demoWebPeerExt.elementHandlersFactories.set('root', new DemoRootElementFactory());
demoWebPeerExt.elementHandlersFactories.set(
    'text-field',
    new DemoTextFieldElementFactory()
);
demoWebPeerExt.elementHandlersFactories.set(
    'demo-messages',
    new DemoMessagesElementFactory()
);
demoWebPeerExt.elementHandlersFactories.set('button', new DemoButtonElementFactory());
