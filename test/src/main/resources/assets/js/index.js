var vm = new Vue({
  el: '#app2',
  data() {
    return {
      contents: []
    }
  },
  created() {
    this.$nextTick(function () {
      this.getData()
      this.getThumbnail()
    })
  },
  methods: {
    getData: function () {
      const self = this
      axios.get('/contents')
        .then(function (response) {
          self.contents = response.data
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  }
})