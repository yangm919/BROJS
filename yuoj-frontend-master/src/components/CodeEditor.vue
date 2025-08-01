<template>
  <div
    id="code-editor"
    ref="codeEditorRef"
    style="min-height: 400px; height: 60vh"
  />
</template>

<script setup lang="ts">
import * as monaco from "monaco-editor";
import {
  onMounted,
  onUnmounted,
  ref,
  toRaw,
  withDefaults,
  defineProps,
  watch,
} from "vue";

/**
 * 定义组件属性类型
 */
interface Props {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}

/**
 * 给组件指定初始值
 */
const props = withDefaults(defineProps<Props>(), {
  value: () => "",
  language: () => "java",
  handleChange: (v: string) => {
    console.log(v);
  },
});

const codeEditorRef = ref();
const codeEditor = ref();
let resizeTimeout: ReturnType<typeof setTimeout>;
let resizeObserver: ResizeObserver;
let originalError: typeof console.error;

watch(
  () => props.language,
  () => {
    if (codeEditor.value) {
      monaco.editor.setModelLanguage(
        toRaw(codeEditor.value).getModel(),
        props.language
      );
    }
  }
);

onMounted(() => {
  if (!codeEditorRef.value) {
    return;
  }

  // 添加 ResizeObserver 错误处理
  originalError = console.error;
  console.error = (...args) => {
    if (
      args[0] &&
      typeof args[0] === "string" &&
      args[0].includes("ResizeObserver")
    ) {
      return; // 忽略 ResizeObserver 警告
    }
    originalError.apply(console, args);
  };

  // Hover on each property to see its docs!
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    automaticLayout: false, // 改为 false 减少 resize 事件
    colorDecorators: true,
    minimap: {
      enabled: true,
    },
    readOnly: false,
    theme: "vs-dark",
    // lineNumbers: "off",
    // roundedSelection: false,
    // scrollBeyondLastLine: false,
  });

  // 手动处理布局调整，使用防抖
  resizeObserver = new ResizeObserver((entries) => {
    // 忽略 ResizeObserver 循环错误
    if (entries.length === 0) return;

    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
      if (codeEditor.value) {
        try {
          toRaw(codeEditor.value).layout();
        } catch (error) {
          // 忽略布局错误
        }
      }
    }, 200); // 增加防抖时间到 200ms
  });

  if (codeEditorRef.value) {
    try {
      resizeObserver.observe(codeEditorRef.value);
    } catch (error) {
      // 忽略观察器错误
    }
  }

  // 编辑 监听内容变化
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue());
  });
});

// 组件卸载时清理资源
onUnmounted(() => {
  if (codeEditor.value) {
    toRaw(codeEditor.value).dispose();
  }
  // 清理 ResizeObserver
  if (resizeObserver) {
    resizeObserver.disconnect();
  }
  // 清理定时器
  if (resizeTimeout) {
    clearTimeout(resizeTimeout);
  }
  // 恢复原始 console.error
  if (originalError) {
    console.error = originalError;
  }
});
</script>

<style scoped></style>
