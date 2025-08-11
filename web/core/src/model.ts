import { api } from './api';


export interface UiElement {
    id: string;
    parent?: UiElement;
    children?: UiElement[];
    serialize: () => any;
    processCommandFromServer: (data: any) => void;
    redraw: () => void;
    init: () => void;
    dispose: () => void;
    tag?: string;
}

export abstract class BaseUiElement implements UiElement {
    parent?: UiElement | undefined;
    init() {
        //noops
    }
    dispose() {
        //noops
    }
    tag?: string | undefined;
    abstract id: string;
    abstract serialize: () => any;
    abstract redraw: () => any;

    children: UiElement[] | undefined = undefined;

    async sendPropertyChange(
        propertyName: string,
        propertyValue: any | null,
        deferred?: boolean
    ) {
        await api.sendCommandAsync(
            this.id,
            'pc',
            {
                pn: propertyName,
                pv: propertyValue,
            },
            deferred
        );
    }

    async sendCommandAsync(commandId: string, commandData?: any) {
        await api.sendCommandAsync(this.id, commandId, commandData, false);
    }

    async makeRequest(commandId: string, commandData?: any) {
        return await api.makeRequest(this.id, commandId, commandData);
    }

    processCommandFromServer(data: any) {
        if ('pc' === data.cmd) {
            const propertyName = data.data.pn;
            const propertyValue = data.data.pv;
            this.updatePropertyValue(propertyName, propertyValue);
        }
    }
    updatePropertyValue(propertyName: string, propertyValue: any) {
        throw new Error(
            `unsupported operation exception: property name = ${propertyName} property value = ${propertyValue}`
        );
    }
}


