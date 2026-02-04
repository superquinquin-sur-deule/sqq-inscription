import axios from 'axios';

// Configure axios defaults for correct array serialization (JAX-RS expects repeated params)
axios.defaults.paramsSerializer = {
  serialize: (params) => {
    const searchParams = new URLSearchParams();
    for (const [key, value] of Object.entries(params)) {
      if (Array.isArray(value)) {
        value.forEach((v) => searchParams.append(key, String(v)));
      } else if (value !== undefined && value !== null) {
        searchParams.append(key, String(value));
      }
    }
    return searchParams.toString();
  },
};
