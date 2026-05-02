let lecturas = [];

const getAll = () => {
  return lecturas;
};

const create = (datos) => {
  const nueva = {
    id: lecturas.length + 1,
    ...datos,
    fecha: new Date(),
  };

  lecturas.push(nueva);

  return nueva;
};

module.exports = {
  getAll,
  create,
};