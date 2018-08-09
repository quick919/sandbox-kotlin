var Hub = new Vue();

new Vue({
  el: "#app2",
  data() {
    return {
      contents: []
    };
  },
  created() {
    this.$nextTick(function() {
      this.getData();
    });
  },
  methods: {
    getData: function() {
      const self = this;
      axios.get("/contents")
        .then(function(response) {
          self.contents = response.data;
        })
        .catch(function(error) {
          console.log(error);
        });
    },
    openModal: function(content) {
      Hub.$emit("open-modal", content);
    },
    closeModal: function() {
      Hub.$emit("close-modal");
    }
  }
});

Vue.component("modal", {
  template: "#modal-template",
  data: function() {
    return {
      active: false,
      content: {}
    };
  },
  methods: {
    open: function(content) {
      this.active = true;
      this.content = content;
    },
    close: function() {
      this.active = false;
    },
    update: function() {
      console.log("update");
    }
  },
  mounted() {
    this.$nextTick(
      function() {
        Hub.$on("open-modal", this.open);
        Hub.$on("close-modal", this.close);
      }.bind(this)
    );
  }
});
