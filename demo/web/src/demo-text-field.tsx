import { BaseDemoUiElement, DemoUiElementFactory } from './common.tsx';

export class DemoTextField extends BaseDemoUiElement {
    constructor(model: any) {
        super([], ['value'], model);
    }
    render() {
        return (
            <input
                type="text"
                className="form-control"
                key={this.id}
                value={this.state.get('value') ?? ''}
                onChange={(e) => {
                    this.stateSetters.get('value')!(e.target.value);
                    this.sendPropertyChange('value', e.target.value, true);
                }}
            />
        );
    }
}

export class DemoTextFieldElementFactory implements DemoUiElementFactory {
    createElement(model: any): BaseDemoUiElement {
        return new DemoTextField(model);
    }
}
