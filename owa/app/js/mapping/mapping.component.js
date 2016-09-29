import template from './mapping.html';
import controller from './mapping.controller.js';

let mappingComponent = {
    restrict: 'E',
    bindings: {
        metadataSources : "<"
    },
    template: template,
    controller: controller,
    controllerAs: 'vm'
};

export default mappingComponent;
