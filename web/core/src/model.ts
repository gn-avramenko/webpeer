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
    serialize(): any {
        return {
            id: this.id,
            tag: this.tag,
            children: this.children?.map((it) => it.serialize()),
        };
    }
    abstract redraw(): void;

    children: BaseUiElement[] | undefined = undefined;

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

    async openWebSocket() {
        await api.openWebSocket(this.id);
    }

    async closeWebSocket() {
        await api.closeWebSocket(this.id);
    }

    async sendCommandAsync(commandId: string, commandData?: any) {
        await api.sendCommandAsync(
            this.id,
            'ac',
            {
                id: commandId,
                data: commandData,
            },
            false
        );
    }

    async makeRequest(commandId: string, commandData?: any) {
        return await api.makeRequest(this.id, commandId, commandData);
    }

    processCommandFromServer(data: any) {
        if ('pc' === data.cmd) {
            const propertyName = data.data.pn;
            const propertyValue = data.data.pv;
            this.updatePropertyValue(propertyName, propertyValue);
            return;
        }
        if ('ac' === data.cmd) {
            const commandId = data.data.commandId;
            const commandData = data.data.commandData;
            this.executeCommand(commandId, commandData);
            return;
        }
    }
    updatePropertyValue(propertyName: string, propertyValue: any) {
        throw new Error(
            `unsupported operation exception: property name = ${propertyName} property value = ${propertyValue}`
        );
    }

    executeCommand(commandId: string, commandData: any) {
        throw new Error(`unsupported operation exception: command id = ${commandId}`);
    }
}
