export interface UiElement {
    id: string
    index: number
    children?: UiElement[]
    serialize: ()=> any
}

export class UiModel{
    rootElement: UiElement = {id: "root", index: 0, serialize: ()=>{}}
}