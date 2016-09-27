class HomeController {
  constructor($stateParams, $rootScope, openmrsNotification) {
    "ngInject"
    $rootScope.links = {};
    $rootScope.links["Manage Mappings"] = "/";

    var vm = this;
    openmrsNotification.routeNotification();

    // Metadata Term Mapping table properties
    vm.enableSearchDefined = true;
    vm.resourceDefined = "metadatamapping/termmapping";
    vm.disableLinksDefined = true;
    vm.limitDefined = 10; //Default value
    vm.customParamsDefined = [
      {
        "property": "mapped",
        "value": "true"
      }
    ];

    vm.columnsDefined = [
      {
        "property": "name",
        "label": "Name"
      },
      {
        "property": "description",
        "label":"Description"
      },
      {
        "property": "metadataSource.display",
        "label":"Source"
      },
      {
        "property": "code",
        "label": "Code"
      },
      {
        "property": "metadataUuid",
        "label":"Referenced UUID"
      }];

    vm.actionsDefined = [
      {
        "action":"view",
        "label":"Set",
        "link":"#/set/",
        "icon":"icon-reply edit-action",
        "showLabel": "true"
      },
      {
        "action":"retire",
        "label":"Retire"
      },
      {
        "action":"unretire",
        "label":"unretire"
      }
    ];



    // Metadata Term Mapping table properties
    vm.resourceUndefined = "metadatamapping/termmapping";
    vm.disableLinksUndefined = true;
    vm.limitUndefined = 10; //Default value
    vm.noDataNotification = 'There are no undefined mappings';

    vm.customParamsUndefined = [
      {
        "property": "mapped",
        "value": "false"
      }
    ];

    vm.columnsUndefined = [
      {
        "property": "name",
        "label": "Name"
      },
      {
        "property": "description",
        "label":"Description"
      },
      {
        "property": "metadataSource.display",
        "label":"Source"
      },
      {
        "property": "code",
        "label": "Code"
      }];
    vm.actionsUndefined = [
      {
        "action":"view",
        "label":"Set",
        "link":"#/set/",
        "icon":"icon-pencil edit-action",
        "showLabel": "true"
      },
      {
        "action":"retire",
        "label":"Retire"
      },
      {
        "action":"unretire",
        "label":"unretire"
      }
    ];

  }
}

export default HomeController;
