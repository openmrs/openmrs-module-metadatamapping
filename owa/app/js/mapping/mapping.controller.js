class MappingController {
  constructor($rootScope, $location, openmrsRest, openmrsNotification) {
    "ngInject"
    $rootScope.links = {};
    $rootScope.links["Manage Mappings"] = "/";
    $rootScope.links["Create Mapping"] = "mapping";

    var vm = this;
    openmrsNotification.routeNotification();

    vm.mapping = {};
    vm.metadataName = "";
    vm.metadataClassEmpty = false;
    vm.suggestions = [];

    vm.cancel = cancel;
    vm.saveMapping = saveMapping;
    vm.onSelect = onSelect;
    vm.getMetadata = getMetadata;
    vm.checkIfEmpty = checkIfEmpty;

    function cancel(){
      $location.path('/');
    }

    function saveMapping(){
      if(angular.isUndefined(vm.metadataName) || vm.metadataName.length === 0){
        delete vm.mapping.metadataUuid;
      }
      openmrsRest.create("metadatamapping/termmapping", vm.mapping).then(handleSuccess, handleException);
    }

    function handleSuccess(success){
      $location.path('/').search({successToast: vm.mapping.name+" has been saved"});
    }

    function handleException(exception){
      openmrsNotification.error(exception.data.error.message);
    }

    function onSelect($item, $model, $label) {
      if(angular.isDefined($item.uuid)){
        vm.mapping.metadataUuid = $item.uuid;
      }
    }

    function getMetadata(val){
      if(angular.isDefined(vm.mapping.metadataClass)){
        vm.suggestions = [];
        openmrsRest.get(getClazz(vm.mapping.metadataClass), {q: vm.metadataName}).then(function(response){
          vm.suggestions = response.results;
        })
      } else {
        vm.metadataClassEmpty = true;
      }
    }

    function checkIfEmpty(){
      if(angular.isDefined(vm.mapping.metadataClass)){
        if(vm.mapping.metadataClass.length !== 0){
          vm.metadataClassEmpty = false;
        } else {
          vm.metadataClassEmpty = true;
        }
      }
    }

    function getClazz(clazz){
      let resource;
      if(clazz.indexOf(".") == -1){
        resource = clazz.toLocaleLowerCase();
      } else if(clazz.indexOf(".module.") == -1){
        let array = clazz.split(".");
        resource = array[array.length-1].toLowerCase();
      } else {
        let array = clazz.split(".");
        resource = array[array.length-2].toLocaleLowerCase() + "/" + array[array.length-1].toLowerCase();
      }
      return resource;
    }

  }
}

export default MappingController;
