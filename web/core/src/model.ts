import { api } from './api';

export abstract class BaseUiElement {
    id: string = '';

    tag?: string;

    parent?: BaseUiElement | undefined;

    protected constructor(model: any) {
        this.id = model.id;
        this.tag = model.tag;
    }

    init() {
        //noops
    }
    dispose() {
        //noops
    }
    getState(): any {
        return {
            id: this.id,
            tag: this.tag,
            children: this.children?.map((it) => it.getState()),
        };
    }
    abstract redraw(): void;

    children: BaseUiElement[] | undefined = undefined;

    async sendPropertyChange(
        propertyName: string,
        propertyValue: any | null,
        deferred?: boolean
    ) {
        await this.sendCommand(
            'pc',
            {
                pn: propertyName,
                pv: propertyValue,
            },
            deferred
        );
    }

    async openWebSocket() {
        await api.openWebSocket(this.id);
    }

    async closeWebSocket() {
        await api.closeWebSocket(this.id);
    }

    async sendCommand(commandId: string, commandData?: any, deferred?: boolean) {
        await api.sendCommandAsync(this.id, commandId, commandData, deferred);
    }

    processCommandFromServer(commandId: string, data?: any) {
        throw new Error(
            `unsupported operation exception: command id = ${commandId} data=${data}`
        );
    }
}
