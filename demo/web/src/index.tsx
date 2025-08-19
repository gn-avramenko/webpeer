import 'webpeer-core';
import { DemoUiElementFactory, demoWebPeerExt } from './common.tsx';
import { DemoRootElementFactory } from './demo-root.tsx';
import './styles.scss';
import { DemoTextFieldElementFactory } from './demo-text-field.tsx';
import { DemoMessagesElementFactory } from './demo-messages-area.tsx';
import { DemoButtonElementFactory } from './demo-button.tsx';
import { webpeerExt } from 'webpeer-core';

const registerFactory = (type: string, factory: DemoUiElementFactory) => {
    webpeerExt.elementTypes = webpeerExt.elementTypes || [];
    webpeerExt.elementTypes.push(type);
    demoWebPeerExt.elementHandlersFactories.set(type, factory);
};

registerFactory('root', new DemoRootElementFactory());
registerFactory('text-field', new DemoTextFieldElementFactory());
registerFactory('demo-messages', new DemoMessagesElementFactory());
registerFactory('button', new DemoButtonElementFactory());
