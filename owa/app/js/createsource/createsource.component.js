import template from './createsource.html';
import controller from './createsource.controller.js';

let addSourceComponent = {
    restrict: 'E',
    bindings: {},
    template: template,
    controller: controller,
    controllerAs: 'vm'
};

export default addSourceComponent;
