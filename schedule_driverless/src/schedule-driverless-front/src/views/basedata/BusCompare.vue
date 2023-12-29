<template>
  <a-table
    :columns="columns"
    :data-source="data"
    bordered
    size="middle"
    :scroll="{ x: 'calc(700px + 50%)', y: 240 }"
  >
    <template slot="streetSlot" slot-scope="text, record">
      <span :style="record.stylecss=='red'?'color:red;':''">{{record.street}}</span>
    </template>
  </a-table>
</template>
<script>
const columns = [
  {
    title: '编辑表（ED）数据',
    children: [
      {
        title: 'Age',
        dataIndex: 'age',
        key: 'age',
        width: 200,
        sorter: (a, b) => a.age - b.age,
      },
      {
        title: 'Street',
        // dataIndex: 'street',
        key: 'street',
        width: 200,
        scopedSlots: {customRender: 'streetSlot'}
      },
      {
        title: 'Building',
        dataIndex: 'building',
        key: 'building',
        width: 100,
      },
      {
        title: 'Door No.',
        dataIndex: 'number',
        key: 'number',
        width: 100,
      }
    ],
  },
  {
    title: '发布表（BS）数据',
    children: [
      {
        title: 'Company Address',
        dataIndex: 'companyAddress',
        key: 'companyAddress',
        width: 200,
      },
      {
        title: 'Company Name',
        dataIndex: 'companyName',
        key: 'companyName',
      },
    ],
  },
];

const data = [];
for (let i = 0; i < 100; i++) {
  var obj = {
    key: i,
    name: 'John Brown',
    age: i + 1,
    street: 'Lake Park',
    building: 'C',
    number: 2035,
    companyAddress: 'Lake Street 42',
    companyName: 'SoftLake Co',
    gender: 'M',
  };

  if (i % 2 === 0) {
    obj.stylecss = 'red';
  }else {
    obj.stylecss = 'green';
  }

  data.push(obj);
}

export default {
  data() {
    return {
      data,
      columns,
    };
  },
};
</script>
