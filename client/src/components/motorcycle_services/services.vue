<template>
  <div class="component-container">
    <div class="searchWrapper">
      <div class="searchWrapper__inputs">
        <p>
          <input
            type="text"
            id="operation"
            name="operation"
            v-model="filteredOperation"
          />
          <label for="operation"
            >Operacja
            <br />
          </label>
        </p>
        <p>
          <input type="text" id="price" name="price" v-model="filteredPrice" />
          <label for="price"
            >Cena
            <br />
          </label>
        </p>
        <p>
          <input
            type="text"
            id="customer"
            name="customer"
            v-model="filteredCustomer"
          />
          <label for="customer"
            >Klient
            <br />
          </label>
        </p>
        <p>
          <input type="text" id="empl" name="empl" v-model="filteredEmployee" />
          <label for="empl"
            >Pracownik
            <br />
          </label>
        </p>
        <p>
          <input type="text" id="mot" name="mot" v-model="filteredMotorcycle" />
          <label for="mot"
            >Motocykl
            <br />
          </label>
        </p>
      </div>
      <div class="searchWrapper__buttons">
        <button
          title="Clear"
          class="my-button clear"
          @click="cleanFilteringCriteria"
        >
          Wyczyść
          <font-awesome-icon class="icon" icon="eraser" />
        </button>
      </div>
    </div>
    <div class="loader-box" v-if="loading">
      <div class="lds-ring">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
      </div>
    </div>
    <div class="action-buttons">
      <router-link
        v-if="checkIsAdmin"
        tag="button"
        title="Add"
        class="my-button add"
        to="/addService"
        >Dodaj
        <font-awesome-icon class="icon" icon="plus" />
      </router-link>
      <button
        v-if="services.length > 0"
        title="Export"
        class="my-button export"
        @click="exportTableToExcel('serwisy')"
      >
        Export
        <font-awesome-icon class="icon" icon="file-export" />
      </button>
    </div>
    <div v-if="services.length > 0" class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th>Operacja</th>
            <th>Cena(PLN)</th>
            <th>Data rozp.</th>
            <th>Data zak.</th>
            <th>Klient</th>
            <th>Pracownik</th>
            <th>Motocykl</th>
          </tr>
        </thead>
        <tbody>
          <router-link
            tag="tr"
            v-for="service in filteredServices"
            :key="service.id"
            :to="`/service/${service.id}`"
          >
            <td>{{ service.operation }}</td>
            <td>{{ service.price }}</td>
            <td>{{ service.startWorkingDate }}</td>
            <td>{{ service.endWorkingDate }}</td>
            <td>{{ service.customer.firstName }}</td>
            <td>{{ service.employee.firstName }}</td>
            <td>{{ service.motorcycleDetails.motorcycleCode }}</td>
          </router-link>
        </tbody>
      </table>
    </div>

    <div v-else class="empty-table">
      <span class="empty-table__header">Brak pozycji</span>
    </div>

  </div>
</template>

<script>
import { serviceUrl } from "@/variables";
import { exportTableToExcel } from "@/helpers";

export default {
  name: "services",
  data() {
    return {
      filteredOperation: "",
      filteredPrice: "",
      filteredCustomer: "",
      filteredEmployee: "",
      filteredMotorcycle: "",
      loading: false,
      services: []
    };
  },
  methods: {
    exportTableToExcel,
    cleanFilteringCriteria() {
      (this.filteredOperation = ""),
        (this.filteredPrice = ""),
        (this.filteredCustomer = ""),
        (this.filteredEmployee = ""),
        (this.filteredMotorcycle = "");
    },
    getServices() {
      this.loading = true;
      this.$http
        .get(`${serviceUrl}`)
        .then(resp => resp.json())
        .then(data => {
          this.loading = false;
          this.services = data;
        })
        .catch(err => {
          this.loading = false;
          console.log(err);
        });
    }
  },
  created() {
    this.getServices();
  },
  computed: {
    filteredServices() {
      return this.services.filter(service => {
        return (
          (!this.filteredOperation ||
            service.operation
              .toLowerCase()
              .includes(this.filteredOperation.toLowerCase())) &&
          (!this.filteredPrice ||
            service.price >= Number(this.filteredPrice)) &&
          (!this.filteredCustomer ||
            service.customer.firstName
              .toLowerCase()
              .includes(this.filteredCustomer.toLowerCase())) &&
          (!this.filteredEmployee ||
            service.employee.firstName
              .toLowerCase()
              .includes(this.filteredEmployee.toLowerCase())) &&
          (!this.filteredMotorcycle ||
            service.motorcycleDetails.motorcycleCode
              .toLowerCase()
              .includes(this.filteredMotorcycle.toLowerCase()))
        );
      });
    },
    checkIsAdmin() {
      return this.$store.getters.isAdmin;
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
</style>
