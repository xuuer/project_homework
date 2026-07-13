<template>
  <div v-html="renderMdText(props.content)"></div>
</template>

<script setup>
import * as marked from 'marked'
import katex from 'katex'

const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

const renderMdText = (text) => {
  // markdown转html
  text = marked.parse(text);
  //text = renderMath(text);
  return text;
}

// markdown转latex
const renderMath = (html) => {
  // 匹配 $$...$$, \[...\], \(...\), and $...$
  const regex =
    /(\$\$([\s\S]+?)\$\$)|(\[([\s\S]+?)\])|(\(([\s\S]+?)\))|(\$([^\$]+?)\$)/g;
  return html.replace(regex, (match, p1, p2, p3, p4, p5, p6, p7, p8) => {
    let latex;
    if (p2) {
      // $$...$$
      latex = p2;
    } else if (p4) {
      // \[...\]
      latex = p4;
    } else if (p6) {
      // \(...\)
      latex = p6;
    } else if (p8) {
      // $...$
      latex = p8;
    }
    if (latex) {
      try {
        return `${katex.renderToString(latex, {
          throwOnError: false,
        })}`;
      } catch (e) {
        console.error("KaTeX rendering error:", e);
        return match;
      }
    }
    return match; // Return original match if no LaTeX was found
  });
}
</script>

<style lang="scss" scoped>
</style>