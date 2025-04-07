import {api} from "../index.ts";

export interface UiElement {
    id: string
    parent?:UiElement
    children?: UiElement[]
    serialize: ()=> any
    executeCommand: (data:any) => void
    tag?:string
}

export abstract class BaseUiElement implements UiElement{
    abstract id: string;
    abstract serialize: () => any;

    children: UiElement[]|undefined = undefined

    async sendPropertyChange(propertyName: string, propertyValue: any|null){
        await api.sendCommand({
            cmd: 'ec',
            id: this.id,
            data: {
                pn: propertyName,
                pv: propertyValue
            }
        })
    }

    executeCommand(data:any){
        if("pc" === data.cmd){
            const propertyName = data.data.pn
            const propertyValue = data.data.pv
            this.updatePropertyValue(propertyName, propertyValue);
        }
    }
    updatePropertyValue(propertyName: string, propertyValue: any) {
        throw new Error(`unsupported operation exception: property name = ${propertyName} property value = ${propertyValue}`)
    }
}

export class UiModel{
    private readonly elements: Map<string, UiElement> = new Map();

    private rootElement?: UiElement

    getRootElement(){
        return this.rootElement
    }
    setRootElement(elm:UiElement) {
        if(elm !== this.rootElement) {
            if (this.rootElement) {
                this.removeNode(this.rootElement)
            }
            this.addNode(elm)
        }
    }

    findNode(id: string){
        return this.elements.get(id)
    }

    removeNode(node: UiElement){
        if(node.parent){
            const idx = node.parent.children!.indexOf(node)
            node.parent.children!.splice(idx, 1)
        }
        this.elements.delete(node.id);
        node.children?.forEach(ch => this.removeNode(ch))
    }

    addNode(node: UiElement, parent?:UiElement){
        if(parent){
            parent.children = parent.children??[]
            if(parent.children.indexOf(node) === -1) {
                parent.children.push(node)
            }
        }
        node.parent = parent
        this.elements.set(node.id, node)
        node.children?.forEach(ch => this.addNode(ch, node))
    }
}