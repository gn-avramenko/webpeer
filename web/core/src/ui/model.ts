export class UiNode{
    id: string = ""
    index = 0
    type: string = ""
    properties?: any
    children: UiNode[] = []
    uiElement:any

    deserialize(obj:any){
        this.id = obj.id
        this.type = obj.type
        this.index = obj.index
        this.properties = obj.properties||{}
        if(obj.children){
            (obj.children as any[]).forEach(item =>{
                const child = new UiNode();
                child.deserialize(item)
                this.children.push(child);
            })
        }
    }
}

export class UiModel extends  UiNode{

}