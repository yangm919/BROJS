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
 * Define component property types
 */
interface Props {
  value: string;
  language?: string;
  handleChange: (v: string) => void;
}

/**
 * Specify initial values for component
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

  // Add ResizeObserver error handling
  originalError = console.error;
  console.error = (...args) => {
    if (
      args[0] &&
      typeof args[0] === "string" &&
      args[0].includes("ResizeObserver")
    ) {
      return; // Ignore ResizeObserver warnings
    }
    originalError.apply(console, args);
  };

  // Hover on each property to see its docs!
  codeEditor.value = monaco.editor.create(codeEditorRef.value, {
    value: props.value,
    language: props.language,
    automaticLayout: false, // Set to false to reduce resize events
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

  // Manually handle layout adjustments with debouncing
  resizeObserver = new ResizeObserver((entries) => {
    // Ignore ResizeObserver loop errors
    if (entries.length === 0) return;

    clearTimeout(resizeTimeout);
    resizeTimeout = setTimeout(() => {
      if (codeEditor.value) {
        try {
          toRaw(codeEditor.value).layout();
        } catch (error) {
          // Ignore layout errors
        }
      }
    }, 200); // Increase debounce time to 200ms
  });

  if (codeEditorRef.value) {
    try {
      resizeObserver.observe(codeEditorRef.value);
    } catch (error) {
      // Ignore observer errors
    }
  }

  // Edit - listen for content changes
  codeEditor.value.onDidChangeModelContent(() => {
    props.handleChange(toRaw(codeEditor.value).getValue());
  });
});

// Clean up resources when component unmounts
onUnmounted(() => {
  if (codeEditor.value) {
    toRaw(codeEditor.value).dispose();
  }
  // Clean up ResizeObserver
  if (resizeObserver) {
    resizeObserver.disconnect();
  }
  // Clean up timer
  if (resizeTimeout) {
    clearTimeout(resizeTimeout);
  }
  // Restore original console.error
  if (originalError) {
    console.error = originalError;
  }
});
</script>

<style scoped></style>
