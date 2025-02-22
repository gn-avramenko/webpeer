import {generateUUID} from "../utils/utils.ts";
import {Middleware, Context} from "../remoting/api.ts";

export type PreloaderHandler = {
  showPreloader: ()=>void
  hidePreloader: () => void
}

export type PreloaderParams = {
  delay: number
  priorityValue?:number
}

export class PreloaderMiddleware implements Middleware{
  protected preloaderShown = false
  protected operations: string[] = []
  priority: number
  constructor(protected handler:PreloaderHandler, protected params:PreloaderParams) {
    this.priority = params.priorityValue??0;
  }
  advice(request: Context, callback: (request: Context) => Promise<Context>): Promise<Context> {
    return new Promise<Context>(async (resolve, reject) =>{
      const operationId = generateUUID()
      try {
        this.operations.push(operationId)
        setTimeout(() => this.showPreloader(), this.params.delay)
        const result = await callback(request)
        resolve(result)
      } catch (e) {
         reject(e)
      } finally {
        this.operations.slice(this.operations.indexOf(operationId), this.operations.indexOf(operationId)+1)
        this.hidePreloader();
      }
    })
  }


  private showPreloader() : void{
     if(!this.preloaderShown && this.operations.length > 0){
       this.handler.showPreloader()
       this.preloaderShown = true
     }
  }

  private hidePreloader() {
     if(this.preloaderShown && this.operations.length > 0){
       this.handler.hidePreloader()
       this.preloaderShown = false
     }
  }
}



