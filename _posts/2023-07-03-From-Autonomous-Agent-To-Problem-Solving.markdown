---
title: "从 Autonomous Agents 到真正的Problem Solving"
layout: post
date: 2023-07-03 22:20
# image: /assets/images/markdown.jpg
headerImage: false
tag:
- Agent
- Domain Specific
- AI
category: blog
author: Xiang Ying
description: 一些对Autonomous Agent Framework的观点
---


随着Andrej Karpathy 和Lilian Weng的发声或发文，_autonomous agent_“终于”被推上了风口浪尖。Silvio Savarese在[这篇博客](https://blog.salesforceairesearch.com/large-action-models/)中提出了一个新词LAM。这一方向也正是这半年多当中，我和mindverse技术团队一直在做的方向。所以借着这股东风，也想简单阐述下自己的思考。

## Autonomous Agent

_Autonomous agents_ are programs, powered by AI, that when given an objective are able to create tasks for themselves, complete tasks, create new tasks, reprioritize their task list, complete the new top task, and loop until their objective is reached. 引自 [Matt Schlicht的blog](https://www.mattprd.com/p/the-complete-beginners-guide-to-autonomous-agents)

简单来说就是：由AI驱动，根据目标自动拆解任务和解决任务，直到目标完成。

[Lilian Weng](https://lilianweng.github.io/posts/2023-06-23-agent/)的Blog里阐述了一个_Autonomous Agent_需要的几大模块：_Planning_模块，_Memory_模块，_Tool use_模块。

<p align="center">
  <img src="/assets/images/lilianweng's architecture.png" alt="Lilian Weng's Architecture">
</p>

图片引自[Lilian Weng's Blog](https://lilianweng.github.io/posts/2023-06-23-agent/)。

在她的blog中，详细阐述了当前各个模块的一些相关学术进展，并且做了比较深度的case study。她的视角可能更倾向于bottom-up的，也就是立足openai的Large Language Model，向上扩展，完成Sama所说的中间层的架设。

而我们的思考更像是top-down的。因为我们从第一天开始就瞄准了认知科学的模拟和脑科学的借鉴。人有海马体，存储了一些长期的记忆。认知科学中，有全局工作空间理论（引用自[维基百科](https://en.wikipedia.org/wiki/Global_workspace_theory)）。该理论认为，全局工作空间是意识生产的地方，working memory 承载了短时思考中，我们将其作为落地为planning的过程和对于对话context的管理等等。

从大的视角上来说，我们的框架（下文统称UMM）如下：
<p align="center">
  <img src="/assets/images/yx_framework.png" alt="Framework">
</p>

## 一些困难

_Autonomous Agent_ 会遇到几个困难，[Lilian的博客](https://lilianweng.github.io/posts/2023-06-23-agent/)中阐述了三点：
1. 有限的上下文长度（Finite context length）
2. 长期规划和任务拆解的困难（Challenges in long-term planning and task decomposition）
3. 大模型接口本身的可靠程度（Reliability of natural language interface）

这些问题已经高度抽象，涵盖面很广，不过在真实打造产品过程中，可能会面对更多的问题：

4. 垂直领域可落地（Domain Specific）：一个通用的Agent不能满足一个人的所有需求，就像有不同职业的人一样，一个人应该有无数个专业的agent来满足他的全方位的需求。而底层的Agent框架，应该需要支持快速打造具备行业深度的_Autonomous Agent_的能力。泛助手的AutoGPT证明了无限可能，但基本上所有的我接触到的使用者给的反馈都是“不是真的有用”。我想主要就是思考模式是一个通用的人，工具也被高度抽象，难以理解场景化的需求。而人的需求，都是场景化的。

5. 语言用户界面（LUI）：尽管语言交互在很多场景中提供了便利，但其效率并不总是最佳。因此，我们需要将图形用户界面（GUI）与语言交互（Language）相结合，创造出一种被称为语言用户界面（LUI）的新型交互模式。LUI的出现极大地提升了交互的效率，并能够提供更为丰富的用户体验。

6. 成长性（Self Learning/Self Taught）：Agent的生命周期不仅局限于单一任务的完成，其应具备持续学习和自我进化的能力。这种成长性使得Agent能从经验中学习，增强对复杂环境的理解，适应不断变化的用户需求。例如，购物Agent通过自我学习不仅可以掌握用户的购物习惯，更能预测市场趋势和用户潜在需求。此外，Agent还需具备自我调整能力，通过反馈循环优化自身行为和策略。

## 解决方案

在UMM框架中，我们考虑了相当一部分的挑战并且试图去克服和解决。当然由于是公司的具体技术，所以不方便在个人文章中进行特别细节的阐述。

### Domain Specific

为了构建能够支持场景特定的Agent的技术框架，我们需要建立一个由五个层次组成的能力体系，自底向上的层次包括：

- 大规模语言模型(LLM)：LLM是我们能力体系的基础，提供基本的认知和表达能力。LLM是Agent理解和表达语言的核心工具。
- API/知识库：这一层提供的是原子级别的行业特定或通用能力。例如，shopify店铺的商品搜索功能可以被封装在此层次以供调用。
- AI Chain：AI Chain层使得具备场景知识的Genius可以注入自己行业的微观逻辑。这些逻辑可以比作销售手册中的操作指南，也可以视为人类思维中的行业逻辑结构。
- Genius的思维框架：在行业微观逻辑结构之上，我们需要一个更大的思维框架来组织和管理这些逻辑。这个框架应侧重于特定场景。例如，在进行旅行社服务时，工作人员会先收集所有必要信息，然后进行后续的推进。这一层为任务拆解提供了指导。
- Genius团队形成的多场景作战小组：这些小组由应对不同的场景化需求的genius组成，甚至中间可能会存在一个项目经理，能够解决更复杂、更多元的问题。这个层次的存在是为了使我们的系统具备处理复杂问题的能力。

在这五个层次的支持下，我们的技术框架将能够有效地支持构建专门用于解决特定场景问题的Agent。

### Context Managment

Context Managment模块旨在解决有限的上下文问题，也是对Working Memory的模拟。

Context Managment中的内容应该包含：

- 设定：包含Genius的设定以及用户自身愿意透露的一些设定。包括基本信息如名字，以及一些行为偏好。Genius可能还有些控制用的instruction。
- 对话：Genius和用户之间的Conversation。
- 用户操作：如点击了页面上的某个Button，对页面进行了切换等。
- Genius操作：如调用API、AI Chain的返回结果。如在知识库中搜索的结果。

Context Managment的使用需要综合考虑时序、相关性，也会考虑过长上下文的压缩。细节不在本文中赘述。

### 成长性

我们在Self 模块实现了多级的成长性。

1. 单点技能的掌握。对于目标明确的任务，例如解决一个特定的问题，我们会自动调整prompt以进行优化和评估，以此来掌握独特的技能。
2. Logic的学习：在工作流程中，通过用户的引导，我们能够形成场景特定的问题解决逻辑。类似于Episodic Memory形成经验的过程。
3. 自我的行为习惯的改造：
  1. Genius的设置会根据进行的对话不断进行调整和修正。
  2. 整体思维模式的改变：如果底层的语言学习模型（LLM）更加开放，那么每个人都可以基于数据拥有一个类似于Lora Adapter的个性化模型。

这一块与我们有类似想法的参考工作有：

- Madaan, A., Tandon, N., Gupta, P., & et al. (2023). [Self-refine: Iterative refinement with self-feedback](https://arxiv.org/abs/2303.17651). arXiv preprint. arXiv:2303.17651.
  
- Pryzant, R., Iter, D., Li, J., & et al. (2023). [Automatic prompt optimization with "gradient descent" and beam search](https://arxiv.org/abs/2305.03495). arXiv preprint. arXiv:2305.03495.
  
- Wang, G., Xie, Y., Jiang, Y., & et al. (2023). [Voyager: An open-ended embodied agent with large language models](https://arxiv.org/abs/2305.16291). arXiv preprint. arXiv:2305.16291.


### LUI

最基本的形式是Citation ID的生成，具体的框架方案待之后再详细阐述。

## 最后

本文最后，宣传一发：我们的公司的产品MindOS.com已经正式上线，欢迎注册使用和提供反馈。

**我坚信AI需要深度的行业从业者来发挥更深更大的价值。**

**我们的产品希望通过MindOS能让新时代的AI普惠到每一个人。**
