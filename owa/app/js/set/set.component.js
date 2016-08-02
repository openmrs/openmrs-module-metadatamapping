import template from './set.html';
import controller from './set.controller.js';

let setComponent = {
    restrict: 'E',
    bindings: {
        mapping : "<"
    },
    template: template,
    controller: controller,
    controllerAs: 'vm'
};

export default setComponent;
