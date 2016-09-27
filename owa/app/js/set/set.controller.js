class SetController {
  constructor($rootScope, $location, openmrsRest, openmrsNotification) {
    "ngInject";
    var vm = this;

    $rootScope.links = {};
    $rootScope.links["Manage Mappings"] = "/";
    $rootScope.links["Set "+vm.mapping.name] = "set/" + +vm.mapping.uuid;

    vm.termmappingResource = "metadatamapping/termmapping";
    vm.enableSearch = true;
    vm.resource = getClass(vm.mapping.metadataClass);
    vm.disableLinks = true;
    vm.limit = 10; //Default value
    vm.customParams = [
      {
        "property": "mapped",
        "value": "true"
      }
    ];

    vm.columns = [
      {
        "property": "name",
        "label": "Name"
      },
      {
        "property": "description",
        "label":"Description"
      },
      {
        "property": "uuid",
        "label": "UUID"
      }];

    vm.actions = [
      {
        "action":"custom",
        "label":"Select",
        "message":"Would you like to map {name} as " + vm.mapping.metadataSource.display + ":" + vm.mapping.code + "?",
        "icon":"icon-ok edit-action",
        "showLabel": "true"
      }
    ];

    vm.cancel = cancel;
    function cancel() {
      $location.path('/').search({});
    }

    vm.unset = unset;
    function unset() {
      vm.mapping.metadataUuid = null;
      openmrsRest.update(vm.termmappingResource, vm.mapping).then(function (success) {
        $location.path('/').search({infoToast: vm.mapping.name+" has been unset"});
      }, handleException)
    }

    vm.confirm = confirm;
    function confirm(selectedItem) {
      vm.mapping.metadataUuid = selectedItem.uuid;
      openmrsRest.update(vm.termmappingResource, vm.mapping).then(handleSuccess, handleException)
    }

    function handleSuccess(success){
      $location.path('/').search({successToast: vm.mapping.name+" has been saved"});
    }
    function handleException(exception){
      openmrsNotification.error(exception.data.error.fieldErrors.name[0].message);
    }
  }


}

function getClass(metadataClass){
  if (angular.isDefined(metadataClass)) {
    let array = metadataClass.split(".");
    return array[array.length-1].toLowerCase();
  }
}

export default SetController;