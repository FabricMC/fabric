# Contributing

## Forward

So, you're looking to contribute to Fabric API. Well, you've come to the right place. This guide is dedicated to teaching you everything you need to know about contributing to Fabric API. This guide might be kind of lengthy, but there's a lot of useful information in it, so make sure to read until the end.

Before we begin, some friendly advice:
1. We don't tend to ban people just for having ideas, no matter how outlandish. However, “no” can mean “no”, and un-constructive responses to constructive arguments can get on our nerves after some time. We are generally unpaid volunteers who dedicate a lot of free time to the tiring job of social interaction - please don't make our work harder than it already is.
2. We're here to help each other learn, after all. Don't hesitate to talk to us at any point! [The Fabric Discord](https://discord.gg/v6v4pMv) is a great place to ask questions and get input on your contributions.
3. Fabric isn't easy to get your content into, as we uphold standards of quality and are very paranoid about making a bad precedent. You will be scrutinized again, and again, and again; you will be asked questions about more-or-less every line in your pull request (even the imports!).

## Knowing When To Aim For Fabric

The first step to contributing to Fabric API is coming up with an idea, and while all ideas may be useful, not all ideas necessarily fit within the scope of Fabric API.

Fabric API tends to be restricted to small, highly used, and breaking changes to the vanilla game. Examples include helpers for adding blocks, which are used by basically every modder ever (hence their inclusion); breaking changes to the rendering system, which ensure that mods can remain compatible with one another; or commonly used mixins that, because of their nature, would otherwise cause mods to be incompatible (think redirects or overwrites). At the end of the day Fabric API exists for ease of implementing and modifying vanilla features, and compatibility between mods.

If your contribution is a massive system, or api for something that doesn't exist in the vanilla game, it does not belong in Fabric API. The best examples are things like, custom world generators, mana systems, or a fluid api. All of these things represent content expansions that don't exist in the base game and should not be included in Fabric API. That doesn't mean however, that your idea is not useful. Larger, more experimental libraries are being developed over at [FabLabs](https://github.com/FabLabsMC). They might be interested in your idea.

If you're unsure of whether or not your idea fits within the scope of Fabric API, do it anyways. Worst case scenario Fabric API denies your pull request, but the community get's a really cool library that does something useful, best case scenario your project is exactly what we're looking for Fabric API. If you don't want to waste your time, however, ask on the Fabric Discord and get the opinion of other members of the community to see if they would appreciate the contribution.

## Taking The First Step

The first step to contributing to Fabric API is making an issue. Head over to the [Fabric API GitHub repository](https://github.com/FabricMC/fabric/issues/) and create a detailed issue outlining what you'd like added to Fabric API. From there, the community can weigh in on whether or not that idea is within the scope of Fabric API, how to best implement the idea, and any additional details that need ironing out.

When making an issue make sure to include specifically what you think the scope of the contribution should be, maybe some specifics about the kinds of methods or structures the contribution would provide, why the contribution is necessary, how many people it would help, and all of the other necessary details to convey exactly how you envision the addition.

Alternatively, if you're just looking to contribute anything to Fabric API, pick an already existing issue that already has some discussion on the topic, and use that as your starting point for your contribution.

## The Hard Part

Idk write some stuff about how to actually write code and test it in the fapi environment

## Style Check

Talk about the checkstyle and the license formatter

## Now The Fun Begins

Talk about the review process

## Last Call

## Celebration Time

Yay cake!
