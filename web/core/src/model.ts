import {api} from "./index";


export abstract class BaseUiElement{
    id: string = ""

    tag?:string
    
    parent?:BaseUiElement;
    
    serialize(){
        return {}
    };

    children: BaseUiElement[]|undefined = undefined

    abstract onChildrenChanged: ()=>void

    constructor(model:any) {
        this.id = model.id;
        this.tag = model.tag;
    }

    async sendPropertyChange(propertyName: string, propertyValue: any|null, deferred?:boolean){
        await api.sendPropertyChanged(this.id, propertyName, propertyValue, deferred)
    }

    async executeAction(actionId: string, actionData?: any){
        await api.sendAction(this.id, actionId, actionData)
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
    private readonly elements: Map<string, BaseUiElement> = new Map();

    private rootElement?: BaseUiElement

    getRootElement(){
        return this.rootElement
    }

    setRootElement(elm:BaseUiElement) {
        if(elm !== this.rootElement) {
            if (this.rootElement) {
                this.removeNode(this.rootElement)
            }
            this.rootElement = elm
            this.addNode(elm)
        }
    }

    findNode(id: string){
        return this.elements.get(id)
    }

    removeNode(node: BaseUiElement){
        if(node.parent){
            const idx = node.parent.children!.indexOf(node)
            node.parent.children!.splice(idx, 1)
        }
        this.elements.delete(node.id);
        node.children?.forEach(ch => this.removeNode(ch))
    }

    addNode(node: BaseUiElement, parent?:BaseUiElement){
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